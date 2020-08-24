/*
 * Copyright 2020 the original author or authors.
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

package org.springframework.cloud.stream.binder.liiklus.properties;

import org.springframework.cloud.stream.binder.BinderSpecificPropertiesProvider;

/**
 * @author Oleg Zhurakousky
 */
public class LiiklusBindingProperties implements BinderSpecificPropertiesProvider {

	private LiiklusConsumerProperties consumer = new LiiklusConsumerProperties();

	private LiiklusProducerProperties producer = new LiiklusProducerProperties();

	public LiiklusConsumerProperties getConsumer() {
		return consumer;
	}

	public void setConsumer(LiiklusConsumerProperties consumer) {
		this.consumer = consumer;
	}

	public LiiklusProducerProperties getProducer() {
		return producer;
	}

	public void setProducer(LiiklusProducerProperties producer) {
		this.producer = producer;
	}

}