/* global Java */

define(['boxing'], function(P) {
    /**
     * Generated constructor.
     * @constructor Command Command
     */
    function Command() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(Command.superclass)
            Command.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "type", {
            get: function() {
                var value = delegate.type;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "parameters", {
            get: function() {
                var value = delegate.parameters;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "command", {
            get: function() {
                var value = delegate.command;
                return P.boxAsJs(value);
            }
        });

        Object.defineProperty(this, "entity", {
            get: function() {
                var value = delegate.entity;
                return P.boxAsJs(value);
            }
        });

    };

    var className = "com.eas.client.changes.Command";
    var javaClass = Java.type(className);
    var ScriptsClass = Java.type("com.eas.script.Scripts");
    var space = ScriptsClass.getSpace();
    space.putPublisher(className, function(aDelegate) {
        return new Command(aDelegate);
    });
    return Command;
});