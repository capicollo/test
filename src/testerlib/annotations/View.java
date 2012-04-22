/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testerlib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Edoardo Savoia
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD})
public @interface View {
    
}
