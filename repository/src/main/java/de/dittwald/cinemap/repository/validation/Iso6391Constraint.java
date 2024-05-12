/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = Iso6391Validator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE,
ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Iso6391Constraint {
    String message() default "Invalid ISO 638-1 language code given";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
