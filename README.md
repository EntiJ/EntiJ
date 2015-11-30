![travis build badge](https://travis-ci.org/EntiJ/EntiJ.svg)
# EntiJ
Entity-Component based, event driven game engine and entity management system written in Java 8 with functional elements.

The core ingredients of the system are the following:
* Entity
* Properties - Arbitrary data related to an entity.
* Logic - Defines the behaviour of an entity.
* Listeners - Triggered when something happens to an entity (such as state change).
* Function - Function that can be attached to entities.
* **Component** - Adds characteristics and functionality to an entity. It may include Properties, Logics, Listeners and Functions.
