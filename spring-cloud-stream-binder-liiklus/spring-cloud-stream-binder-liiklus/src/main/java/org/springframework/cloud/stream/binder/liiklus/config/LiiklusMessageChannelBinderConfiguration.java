/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.liiklus.config;

import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.cloud.stream.binder.liiklus.LiiklusMessageChannelBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * Configuration class for Liiklus message channel binder.

 * @author Oleg Zhurakousky
 */
@Configuration
@Import({ PropertyPlaceholderAutoConfiguration.class })
//@EnableConfigurationProperties({ RabbitBinderConfigurationProperties.class,
//		RabbitExtendedBindingProperties.class })
public class LiiklusMessageChannelBinderConfiguration {


	@Bean
	LiiklusMessageChannelBinder liiklusMessageChannelBinder(FunctionCatalog catalog) {
		LiiklusMessageChannelBinder binder = new LiiklusMessageChannelBinder(catalog);
		return binder;
	}
}
