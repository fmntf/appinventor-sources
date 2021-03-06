// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0
/**
 * @license
 * @fileoverview UDOO Blockly-blocks utilities for MIT App Inventor.
 * @author francesco.monte@gmail.com (Francesco Montefoschi)
 */

'use strict';


Blockly.Blocks['udoo_uno_digital_pinout'] = {
    init: function () {
        var pinList = [];
        for (var i=0; i<=13; i++) {
            pinList.push([''+i, ''+i]);
        }
        
        var dropdown = new Blockly.FieldDropdown(pinList);
      
        this.setColour(Blockly.MATH_CATEGORY_HUE);
        this.setOutput(true, Blockly.Blocks.Utilities.YailTypeToBlocklyType("text", Blockly.Blocks.Utilities.OUTPUT));
        this.appendDummyInput().appendField(dropdown, 'UDOO_UNO_DIGITAL_PINOUT');
        var thisBlock = this;
    }
};

Blockly.Blocks['udoo_due_digital_pinout'] = {
    init: function () {
        var start = 0,
            incrementStep = 10,
            originalPin = undefined,
            pinList = [];
        for (var i=0; i<=53; i++) {
            pinList.push([''+i, ''+i]);
        }
        
        var listGen = function() {
            originalPin = this.value_;
            var list = pinList.slice(start, start+incrementStep);
            if (start>0) list.unshift(['Back...', 'Back']);
            if (start+incrementStep<pinList.length) list.push(['More...', 'More']);        
            return list;
        };
      
        var dropdown = new Blockly.FieldDropdown(listGen, function(selection){
            var ret = undefined;

            if (selection == "More" || selection == "Back") {  
                if (selection == "More") start += incrementStep;
                else start -= incrementStep;

                var t = this;
                setTimeout(function(){t.showEditor_();},1);

                return originalPin;
            }
        });
      
        this.setColour(Blockly.MATH_CATEGORY_HUE);
        this.setOutput(true, Blockly.Blocks.Utilities.YailTypeToBlocklyType("text", Blockly.Blocks.Utilities.OUTPUT));
        this.appendDummyInput().appendField(dropdown, 'UDOO_DUE_DIGITAL_PINOUT');
        var thisBlock = this;
    }
};

Blockly.Blocks['udoo_analog_pinout'] = {
    init: function () {
        var pinList = [];
        for (var i=0; i<=7; i++) {
            pinList.push(['A'+i, 'A'+i]);
        }
        
        var dropdown = new Blockly.FieldDropdown(pinList);
      
        this.setColour(Blockly.MATH_CATEGORY_HUE);
        this.setOutput(true, Blockly.Blocks.Utilities.YailTypeToBlocklyType("text", Blockly.Blocks.Utilities.OUTPUT));
        this.appendDummyInput().appendField(dropdown, 'UDOO_ANALOG_PINOUT');
        var thisBlock = this;
    }
};

Blockly.Blocks['arduino_pin_value'] = {
    init: function () {
        var dropdown = new Blockly.FieldDropdown([['HIGH', 'HIGH'], ['LOW', 'LOW']]);

        this.setColour(Blockly.LOGIC_CATEGORY_HUE);
        this.setOutput(true, Blockly.Blocks.Utilities.YailTypeToBlocklyType("text", Blockly.Blocks.Utilities.OUTPUT));
        this.appendDummyInput().appendField(dropdown, 'ARDUINO_PIN_VALUE');
        var thisBlock = this;
    }
};

Blockly.Blocks['arduino_pin_mode'] = {
    init: function () {
        var dropdown = new Blockly.FieldDropdown([['INPUT', 'INPUT'], ['OUTPUT', 'OUTPUT']]);

        this.setColour(Blockly.LOGIC_CATEGORY_HUE);
        this.setOutput(true, Blockly.Blocks.Utilities.YailTypeToBlocklyType("text", Blockly.Blocks.Utilities.OUTPUT));
        this.appendDummyInput().appendField(dropdown, 'ARDUINO_PIN_MODE');
        var thisBlock = this;
    }
};

Blockly.Blocks['arduino_interrupt_mode'] = {
    init: function () {
        var dropdown = new Blockly.FieldDropdown([['CHANGE', 'CHANGE'], ['FALLING', 'FALLING'], ['RISING', 'RISING']]);
        
        this.setColour(Blockly.LOGIC_CATEGORY_HUE);
        this.setOutput(true, Blockly.Blocks.Utilities.YailTypeToBlocklyType("text", Blockly.Blocks.Utilities.OUTPUT));
        this.appendDummyInput().appendField(dropdown, 'ARDUINO_INTERRUPT_MODE');
        var thisBlock = this;
    }
};
