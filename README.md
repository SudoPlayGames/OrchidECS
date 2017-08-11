# OrchidECS

An entity component system (ECS) for the JVM inspired by Artemis and Terrasology.

## Summary

### World

The world is the heart of the module and the entry point for users.

#### The world:

* Manages a collection of entities
* Creates entities and provides requested entities
* Can serialize and deserialize its entity collection
* Publishes entity events
* Subscribes and injects annotated objects; retains only weak reference to subscribers

#### World Updates:

* World#update must be called to process: entity add / remove, component add / remove
* Changes to component fields will propagate and be available immediately.
* Changes to entity component composition, along with entity addition and removal, will propagate to entity sets the next time World#update is called.

### Entity Reference

* Entity references, or entities,  are objects that wrap a primitive long as the entity’s id and expose convenient methods that delegate to world methods.
* Entity id’s are never reused.
* Entity id’s are not exposed to the user, usage of the reference object is encouraged.
* Entities ids are internally mapped to sets of components.
* When Entity#worldRemove is called, the entity is immediately invalidated.

### Component

* Components are extended from Component and provided by the user.
* Component classes must have a no-args constructor for serialization.
* Component classes must be registered with the WorldBuilder during world creation.
* Components are automatically persisted by the world.
  * Component classes annotated with @Transient will not be persisted.
  * Component class member fields with the transient modifier will not be persisted.
  * Component class member fields annotated with @Transient will not be persisted.

### Component Mapper

* Component mappers map components to entities and provide fast, easy component retrieval for entities.
* Component mappers provide component iteration by component type.
* Component mappers are retained by strong reference and reused.
* Component mappers are injected into annotated objects by the WorldBuilder during world creation or by calling World#registerSystem.
* Component mappers are read only.

### Entity Set

* Entity sets are collections of entities that are filtered by their component makeup and synchronized with the world.
* Entity sets are retained by strong reference, indexed by filter, and reused.
* Entity sets are injected into annotated objects registered with the WorldBuilder during world creation.
* Entity sets are read-only.

### Entity Set Event Deque

* Entity sets have two events, add and remove, that can be accumulated and polled using deques.
* Entity set deques are created by calling EntitySet#newDequeEventEntityAdd and EntitySet#newDequeEventEntityRemove.
* Entity set deques are created for each request and retained by weak reference.
  * Entity set deques should be handled with care; a deque will start accumulating entity references as soon as it is created and is only ever cleared of references when it is polled. A deque that is not dereferenced properly can go un-polled and can be a potential memory leak.

### Event Handler

* An event handler is any class method annotated with @Subscribe with exactly one parameter: an entity event extended from EntityEventBase.
* Event handlers are retained as weak references.
* Event handlers can be filtered by the event entity’s component makeup.
* Event handler priority is per filter; ie. event handlers priority is only relevant when more than one event handler is listening for the same event in the same filter group.
* Event handlers without a filter are called before the filtered handler groups.
* The order in which the filtered handler groups are processed is not guaranteed.

### Injected Field

* An injected field is any class field that is annotated with:
  * @InjectComponentMapper
  * @InjectEntitySet
  * @InjectWorld

### System

* A system is any class with at least one event handler method or injected field.
* A system can be any class and doesn’t require inheritance from any class in this module.
* Systems must be registered either with the WorldBuilder during world creation or by calling World#registerSystem after the world has been created.
* Systems don’t communicate directly
* Systems communicate by subscribing to and publishing entity events.
* Systems should not maintain any game state, use components instead.
* Systems are not retained, only weak references to a system’s annotated event handler methods are retained. It is left to the user to manage system lifecycles.

### Entity Event

* Entity events can be published via World#publish
* A single entity event instance can only target one entity
* Entity events are delivered to event handlers immediately and in the order the events are received.
* Entity events can be cancelled by calling EntityEventBase#cancel.
* An entity event that has been cancelled will cease propagation immediately.

### Configuration

* Implement EntityStoreSerializer, WorldReader, WorldSerializer, and WorldWriter to provide your own serialization implementation. To set the serializer, call either WorldBuilder#setWorldSerializer or World#setWorldSerializer.
* Implement components by extending ComponentBase.
* Implement entity events by extending EntityEventBase.

### Considerations
* Fails fast when:
  * A non-serializable component is registered.
  * A duplicate component class is registered.
