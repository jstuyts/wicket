/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

;(function (wicket, preact) {
	function createNode(element) {
		var props = {};
		for (var attributeIndex = 0; attributeIndex < element.attributes.length; attributeIndex++) {
			var attribute = element.attributes[attributeIndex];
			props[attribute.name] = attribute.value;
		}

		var children = [];
		if (element.childNodes.length > 0) {
			for (var childIndex = 0; childIndex < element.childNodes.length; childIndex++) {
				var child = element.childNodes[childIndex];
				if (child.nodeType === Node.ELEMENT_NODE) {
					children.push(createNode(child));
				} else if (child.nodeType === Node.TEXT_NODE) {
					children.push(child.textContent);
				}
			}
		}

		return preact.h(element.tagName.toLowerCase(), props, children);
	}

	function createVirtualDOM(htmlString) {
		var parser = new DOMParser();
		var doc = parser.parseFromString(htmlString, "text/html");
		var elementNode = doc.body.firstElementChild;

		return createNode(elementNode);
	}

	wicket.DOM.registerReplacementMethod("preact", function (element, text) {
		if (element.parentElement.childElementCount === 1) {
			preact.render(createVirtualDOM(text), element.parentElement);
		} else {
			wicket.Log.error("Preact replacement: element with ID: " + element.id + ", is not the only element in its parent.");
		}
	});
})(Wicket, preact);
