//
//  (c) Copyright 2003-2014 Methics Oy. All rights reserved.
//
package fi.methics.laverca.rest.json;

/**
 * InputParameter of a {@link UseCase}.
 * InputParameter consists of multiple parameter attributes defined in {@link Param}.
 * 
 * <p><b>Example of a typical InputParam:</b>
 * <pre>
    "{@link Input}":
       {
         "{@link Param#Name Name}":"ApID",
         "{@link Param#Value Value}":"http://test2_ap",
       }
 * </pre>
 * JSON name: "Input"
 */
public class Input extends Param {

    /**
     * Default constructor
     */
    public Input() {
        super();
    }
    
    public Input(final String name, final String value) {
        this.Name  = name;
        this.Value = value;
    }
    
    @Override
    public String toString() {
        return "Input [Name=" + this.Name +
               ", Value=" + this.Value + ", Required=" + this.Required + "]";
    }

}
