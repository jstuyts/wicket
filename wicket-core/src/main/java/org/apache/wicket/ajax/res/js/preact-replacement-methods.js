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
	function createNode(element, optionalNamespaceUri) {
		var props = {};
		if (optionalNamespaceUri) props.xmlns = optionalNamespaceUri;
		for (var attributeIndex = 0; attributeIndex < element.attributes.length; attributeIndex++) {
			var attribute = element.attributes[attributeIndex];
			props[attribute.name] = attribute.value;
		}

		var children = Array(element.childNodes.length);
		if (children.length > 0) {
			for (var childIndex = 0; childIndex < element.childNodes.length; childIndex++) {
				var child = element.childNodes[childIndex];
				if (child.nodeType === Node.ELEMENT_NODE) {
					children[childIndex] = createNode(child, optionalNamespaceUri);
				} else if (child.nodeType === Node.TEXT_NODE) {
					children[childIndex] = child.textContent;
				}
			}
		}

		return preact.h(element.tagName.toLowerCase(), props, children);
	}

	function createVirtualDom(htmlString) {
		return createNode(Document.parseHTMLUnsafe(htmlString).body.firstElementChild);
	}

	function createVirtualXmlDom(xmlString) {
		var dom = new DOMParser().parseFromString(xmlString, "application/xml");
		return createNode(dom.firstElementChild, dom.firstElementChild.namespaceURI);
	}

	wicket.DOM.registerReplacementMethod("preact", function (element, text) {
		if (element.parentElement.childElementCount === 1) {
			preact.render(createVirtualDom(text), element.parentElement);
		} else {
			wicket.Log.error("Preact replacement: element with ID: " + element.id + ", is not the only element in its parent.");
		}
	});

	wicket.DOM.registerReplacementMethod("preact-xml", function (element, text) {
		if (element.parentElement.childElementCount === 1) {
			preact.render(createVirtualXmlDom(text), element.parentElement);
		} else {
			wicket.Log.error("Preact replacement: element with ID: " + element.id + ", is not the only element in its parent.");
		}
	});
})(Wicket, preact);
