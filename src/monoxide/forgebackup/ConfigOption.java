package monoxide.forgebackup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.common.Configuration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigOption {
	String name() default "";
	String comment() default "";
	String section() default Configuration.CATEGORY_GENERAL;
}
