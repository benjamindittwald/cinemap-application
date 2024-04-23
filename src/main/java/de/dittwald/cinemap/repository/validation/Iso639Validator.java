/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Iso639Validator implements ConstraintValidator<Iso639Constraint, String> {
    @Override
    public void initialize(Iso639Constraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String langcode, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/iso-639-3_Name_Index.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\t");

                if (StringUtils.equals(fields[0], langcode)) {
                    valid = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            // Todo: Add Exceptionhandler to send 500
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return valid;
    }
}
