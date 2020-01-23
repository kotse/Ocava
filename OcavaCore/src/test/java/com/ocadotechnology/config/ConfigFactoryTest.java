/*
 * Copyright © 2017-2020 Ocado (Ocava)
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
package com.ocadotechnology.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSet;
import com.ocadotechnology.config.ConfigManager.PrefixedProperty;
import com.ocadotechnology.config.TestConfig.FirstSubConfig;
import com.ocadotechnology.testing.InternalClassTest;
import com.ocadotechnology.testing.UtilityClassTest;

@DisplayName("The ConfigFactory utility class")
class ConfigFactoryTest implements UtilityClassTest, InternalClassTest {

    @Override
    public Class<?> getTestSubject() {
        return ConfigFactory.class;
    }

    @Nested
    @DisplayName("read() method")
    class ReadMethodTests {

        protected Config<TestConfig> readConfigFromProperties(Properties props, ImmutableSet<PrefixedProperty> prefixedProperties) throws ConfigKeysNotRecognisedException {
            return ConfigFactory.read(TestConfig.class, props::getProperty, prefixedProperties);
        }

        @Test
        @DisplayName("with all properties provided")
        void allPropertiesProvided() throws ConfigKeysNotRecognisedException {
            Properties props = fullySpecifiedProperties();
            Config<TestConfig> config = readConfigFromProperties(props, ImmutableSet.of());

            assertSoftly(softly -> {
                softly.assertThat(config.getInt(TestConfig.FOO)).isEqualTo(1);
                softly.assertThat(config.getInt(TestConfig.BAR)).isEqualTo(2);
                softly.assertThat(config.getInt(FirstSubConfig.WOO)).isEqualTo(3);
                softly.assertThat(config.getInt(FirstSubConfig.HOO)).isEqualTo(4);
                softly.assertThat(config.getInt(FirstSubConfig.SubSubConfig.X)).isEqualTo(5);
                softly.assertThat(config.getInt(FirstSubConfig.SubSubConfig.Y)).isEqualTo(6);
            });
        }

        @Test
        @DisplayName("with prefixed properties")
        void prefixedPropertiesProvided() throws ConfigKeysNotRecognisedException {
            Properties props = fullySpecifiedProperties();
            PrefixedProperty prefixedProperty = new PrefixedProperty("Prefix1@TestConfig.FOO", "7");
            Config<TestConfig> config = readConfigFromProperties(props, ImmutableSet.of(prefixedProperty));

            assertSoftly(softly -> {
                softly.assertThat(config.getPrefixedConfigItems("Prefix1").getInt(TestConfig.FOO)).isEqualTo(7);
            });
        }

        @Test
        @DisplayName("does not include config for missing properties")
        void notAllPropertiesProvided() throws ConfigKeysNotRecognisedException {
                Config<TestConfig> config = readConfigFromProperties(new Properties(), ImmutableSet.of());
                assertThat(config.containsKey(TestConfig.FOO)).isFalse();
        }
    }

    private static Properties fullySpecifiedProperties() {
        Properties props = new Properties();
        props.setProperty("TestConfig.FOO", "1");
        props.setProperty("TestConfig.BAR", "2");
        props.setProperty("TestConfig.FirstSubConfig.WOO", "3");
        props.setProperty("TestConfig.FirstSubConfig.HOO", "4");
        props.setProperty("TestConfig.FirstSubConfig.SubSubConfig.X", "5");
        props.setProperty("TestConfig.FirstSubConfig.SubSubConfig.Y", "6");
        return props;
    }
}
