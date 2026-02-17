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

/*
	Note: these tests run only through Web Server.
	Here is a possible setup for Apache HTTPD:

		 Alias /ajax-tests "/path/to/wicket/wicket-core/src"

		 <Directory "/path/to/wicket/wicket-core/src">

		 Options Indexes
		 AllowOverride None AuthConfig

		 Order allow,deny
		 Allow from all

		 </Directory>

	Or start StartJavaScriptTests.java in project wicket-js-tests.
 */

/*global ok: true, start: true, test: true, equal: true, deepEqual: true,
 QUnit: true, expect: true, console: true  */

jQuery(document).ready(function() {
	"use strict";

	const {module, test} = QUnit;

	var execute = function (attributes, assert, done) {
		const done2 = done || assert.async();
		Wicket.testDone = done2;

		var defaults = {
			fh: [
				function () {
					done2();
					assert.ok(false, 'Failure handler should not be executed!');
				}
			],
			ch: '0|s',
			sh: [
				function () {
					assert.ok(true, 'Success handler is executed');
				}
			]
		};
		var attrs = jQuery.extend({}, defaults, attributes);
		var call = new Wicket.Ajax.Call();
		call.ajax(attrs);

	};

	// Ajax tests are executed only when run with Web Server
	if (!QUnit.isLocal) {

		module('Wicket.Ajax', {
			beforeEach: function () {
				// unsubscribe all global listeners
				Wicket.Event.unsubscribe();
			}
		});

		test('processComponent(), Preact, normal case.', assert => {
			const done = assert.async();
			assert.expect(2);

			assert.equal(jQuery('#componentToReplace').text(), 'old body', 'The component is existing and has the old innerHTML');

			var attrs = {
				u: 'data/ajax/componentIdPreact.xml',
				c: 'componentIdPreact',
				sh: [
					function () {
						done();
						assert.equal(jQuery('#componentToReplace').text(), 'new body', 'The component must be replaced');
					}
				]
			};
			execute(attrs, assert, done);
		});

		test('processComponent(), Preact, replace a component with a table with scripts inside.', assert => {
			const done = assert.async();
			Wicket.testDone = done;
			Wicket.assert = assert;
			assert.expect(4);

			var attrs = {
				u: 'data/ajax/complexComponentIdPreact.xml',
				c: 'complexComponentIdPreact',
				sh: [
					function () {
						done();
						assert.equal(jQuery('#componentToReplace')[0].tagName.toLowerCase(), 'table', 'A component with id \'componentToReplace\' must be a table now!');
					}
				]
			};
			execute(attrs, assert, done);
		});

		test('processComponent(), Preact, log error if element to replace not only child element.', assert => {
			const done = assert.async();
			assert.expect(3);

			var oldWicketLogError = Wicket.Log.error;

			Wicket.Log.error = function () {
				assert.equal(arguments[0], "Preact replacement: element with ID: componentToReplaceNotOnlyChild, is not the only element in its parent.", "Element that is not only child element is logged.");

				// restore the original method
				Wicket.Log.error = oldWicketLogError;
			};

			assert.equal(jQuery('#componentToReplaceNotOnlyChild').text(), 'old body', 'The component is existing and has the old innerHTML');

			var attrs = {
				u: 'data/ajax/componentIdNotOnlyChildPreact.xml',
				c: 'componentIdNotOnlyChildPreact',
				sh: [
					function () {
						done();
						assert.equal(jQuery('#componentToReplaceNotOnlyChild').text(), 'old body', 'The component is not replaced');
					}
				]
			};
			execute(attrs, assert, done);
		});
	}
});
