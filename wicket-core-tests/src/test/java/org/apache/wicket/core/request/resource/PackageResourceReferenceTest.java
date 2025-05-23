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
package org.apache.wicket.core.request.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.wicket.Application;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.core.util.resource.UrlResourceStream;
import org.apache.wicket.core.util.resource.locator.IResourceStreamLocator;
import org.apache.wicket.core.util.resource.locator.caching.CachingResourceStreamLocator;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.AbstractResource.ContentRangeType;
import org.apache.wicket.request.resource.CssPackageResource;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.JavaScriptPackageResource;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.ResourceReference.UrlAttributes;
import org.apache.wicket.response.ByteArrayResponse;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Pedro Santos
 */
class PackageResourceReferenceTest extends WicketTestCase
{
	private static Class<PackageResourceReferenceTest> scope = PackageResourceReferenceTest.class;
	private static final Locale defaultLocale = Locale.CHINA;
	private static final Locale[] locales = { null, new Locale("en"), new Locale("en", "US") };
	private static final String[] styles = { null, "style" };
	private static final String[] variations = { null, "var" };

	/**
	 * @throws Exception
	 */
	@BeforeEach
	void before()
	{
		// some locale outside those in locales array
		tester.getSession().setLocale(Locale.CHINA);
	}

	/**
	 * 
	 */
	@Test
	void resourceResolution()
	{
		for (Locale locale : locales)
		{
			for (String style : styles)
			{
				for (String variation : variations)
				{
					ResourceReference reference = new PackageResourceReference(scope,
						"resource.txt", locale, style, variation);
					UrlAttributes urlAttributes = reference.getUrlAttributes();
					assertEquals(locale, urlAttributes.getLocale());
					assertEquals(style, urlAttributes.getStyle());
					assertEquals(variation, urlAttributes.getVariation());

					ByteArrayResponse byteResponse = new ByteArrayResponse();
					Attributes mockAttributes = new Attributes(tester.getRequestCycle()
						.getRequest(), byteResponse);
					reference.getResource().respond(mockAttributes);
					String fileContent = new String(byteResponse.getBytes());
					if (locale != null)
					{
						assertTrue(fileContent.contains(locale.getLanguage()));
						if (locale.getCountry() != null)
						{
							assertTrue(fileContent.contains(locale.getCountry()));
						}
					}
					if (style != null)
					{
						assertTrue(fileContent.contains(style));
					}
					if (variation != null)
					{
						assertTrue(fileContent.contains(variation));
					}
				}
			}
		}
	}

	@Test
	void resourceResponse()
	{
		for (Locale locale : locales)
		{
			for (String style : styles)
			{
				for (String variation : variations)
				{
					ResourceReference reference = new PackageResourceReference(scope,
						"resource.txt", locale, style, variation);

					ByteArrayResponse byteResponse = new ByteArrayResponse();
					Attributes mockAttributes = new Attributes(tester.getRequestCycle()
						.getRequest(), byteResponse);
					reference.getResource().respond(mockAttributes);
					String fileContent = new String(byteResponse.getBytes());
					if (locale != null)
					{
						assertTrue(fileContent.contains(locale.getLanguage()));
						if (locale.getCountry() != null)
						{
							assertTrue(fileContent.contains(locale.getCountry()));
						}
					}
					if (style != null)
					{
						assertTrue(fileContent.contains(style));
					}
					if (variation != null)
					{
						assertTrue(fileContent.contains(variation));
					}
				}
			}
		}
	}

	/**
	 * Asserting if user did not set any locale or style, those from session get used if any
	 */
	@Test
	void sessionAttributesRelevance()
	{
		for (Locale locale : new Locale[] { new Locale("en"), new Locale("en", "US") })
		{
			tester.getSession().setLocale(locale);
			for (String style : styles)
			{
				tester.getSession().setStyle(style);
				for (String variation : variations)
				{
					ResourceReference reference = new PackageResourceReference(scope,
						"resource.txt", null, null, variation);
					UrlAttributes urlAttributes = reference.getUrlAttributes();
					assertEquals(tester.getSession().getLocale(), urlAttributes.getLocale());
					assertEquals(tester.getSession().getStyle(), urlAttributes.getStyle());
					assertEquals(variation, urlAttributes.getVariation());
				}
			}
		}
	}

	/**
	 * Assert preference for specified locale and style over session ones
	 */
	@Test
	void userAttributesPreference()
	{
		tester.getSession().setLocale(new Locale("en"));
		tester.getSession().setStyle("style");

		Locale[] userLocales = { null, new Locale("pt"), new Locale("pt", "BR") };
		String userStyle = "style2";

		for (Locale userLocale : userLocales)
		{
			for (String variation : variations)
			{
				ResourceReference reference = new PackageResourceReference(scope, "resource.txt",
					userLocale, userStyle, variation);
				UrlAttributes urlAttributes = reference.getUrlAttributes();

				assertEquals(userLocale, urlAttributes.getLocale());
				assertEquals(userStyle, urlAttributes.getStyle());
				assertEquals(variation, urlAttributes.getVariation());
			}
		}
	}

	/**
	 * see WICKET-5251 : Proper detection of already minified resources
	 */
	@Test
	void testMinifiedNameDetectMinInName()
	{
		class PRR extends PackageResourceReference
		{

			public PRR(String key) {
				super(key);
			}

			// make it public for the test
			@Override
			public String getMinifiedName() {
				return super.getMinifiedName();
			}
		}

		final PRR html5minjs = new PRR("html5.min.js");
		assertEquals("html5.min.js", html5minjs.getMinifiedName());

		final PRR html5notminjs = new PRR("html5.notmin.js");
		assertEquals("html5.notmin.min.js", html5notminjs.getMinifiedName());

		final PRR html5notmin = new PRR("html5notmin");
		assertEquals("html5notmin.min", html5notmin.getMinifiedName());

		final PRR html5min = new PRR("html5.min");
		assertEquals("html5.min", html5min.getMinifiedName());

	}

	/**
	 * see WICKET-5250 - for JavaScriptResourceReference
	 */
	@Test
	void testJavaScriptResourceReferenceRespectsMinifiedResourcesDetection()
	{
		Application.get().getResourceSettings().setUseMinifiedResources(true);
		final JavaScriptResourceReference notMinified = new JavaScriptResourceReference(PackageResourceReferenceTest.class, "a.js");
		final JavaScriptPackageResource notMinifiedResource = notMinified.getResource();
		assertTrue(notMinifiedResource.getCompress(), "Not minified resource should got its compress flag set to true");

		final JavaScriptResourceReference alreadyMinified = new JavaScriptResourceReference(PackageResourceReferenceTest.class, "b.min.js");
		final JavaScriptPackageResource alreadyMinifiedResource = alreadyMinified.getResource();
		assertFalse(alreadyMinifiedResource.getCompress(), "Already minified resource should got its compress flag set to false");
	}

	/**
	 * see WICKET-5250 - for CSSResourceReference
	 */
	@Test
	void testCSSResourceReferenceRespectsMinifiedResourcesDetection()
	{
		Application.get().getResourceSettings().setUseMinifiedResources(true);
		final CssResourceReference notMinified = new CssResourceReference(PackageResourceReferenceTest.class, "a.css");
		final CssPackageResource notMinifiedResource = notMinified.getResource();
		assertTrue(notMinifiedResource.getCompress(), "Not minified resource should got its compress flag set to true");

		final CssResourceReference alreadyMinified = new CssResourceReference(PackageResourceReferenceTest.class, "b.min.css");
		final CssPackageResource alreadyMinifiedResource = alreadyMinified.getResource();
		assertFalse(alreadyMinifiedResource.getCompress(), "Already minified resource should got its compress flag set to false");
	}

	/**
	 * See WICKET-5819 - Media tags
	 */
	@Test
	void testContentRange()
	{
		// Test range
		assertEquals("resource", makeRangeRequest("bytes=0-7"));
		assertEquals("ource", makeRangeRequest("bytes=3-7"));
		assertEquals("resource_var_style_en.txt", makeRangeRequest("bytes=0-"));
		assertEquals("var_style_en.txt", makeRangeRequest("bytes=9-"));
		assertEquals("resource_var_style_en.txt", makeRangeRequest("bytes=-"));
		assertEquals("resource_var_style_en.txt", makeRangeRequest("bytes=-25"));
	}

	private String makeRangeRequest(String range)
	{
		ResourceReference reference = new PackageResourceReference(scope, "resource.txt",
			locales[1], styles[1], variations[1]);

		ByteArrayResponse byteResponse = new ByteArrayResponse();

		Request request = tester.getRequestCycle().getRequest();
		MockHttpServletRequest mockHttpServletRequest = (MockHttpServletRequest)request.getContainerRequest();
		mockHttpServletRequest.setHeader("range", range);
		Attributes mockAttributes = new Attributes(request, byteResponse);
		reference.getResource().respond(mockAttributes);
		return new String(byteResponse.getBytes());
	}

	/**
	 * See WICKET-5819 - Media tags
	 *
	 * @throws IOException
	 */
	@Test
	void testContentRangeLarge() throws IOException
	{
		InputStream resourceAsStream = null;
		try
		{
			resourceAsStream = PackageResourceReferenceTest.class.getResourceAsStream("resource_gt_4096.txt");
			String content = new String(IOUtils.toByteArray(resourceAsStream));

			// Check buffer comprehensive range request
			String bytes4094_4105 = makeRangeRequestToBigResource("bytes=4094-4105");
			assertEquals(12, bytes4094_4105.length());
			assertEquals("River Roller", bytes4094_4105);

			// Check buffer exceeding range request
			String bytes1000_4999 = makeRangeRequestToBigResource("bytes=1000-4999");
			assertEquals(4000, bytes1000_4999.length());
			assertEquals(content.substring(1000, 5000), bytes1000_4999);

			// Check buffer exceeding range request until end of content
			String bytes1000_end = makeRangeRequestToBigResource("bytes=1000-");
			assertEquals(4529, bytes1000_end.length());
			assertEquals(content.substring(1000), bytes1000_end);

			// Check complete range request
			assertEquals(content.length(), makeRangeRequestToBigResource("bytes=-").length());
		}
		finally
		{
			IOUtils.closeQuietly(resourceAsStream);
		}
	}

	private String makeRangeRequestToBigResource(String range)
	{
		ResourceReference reference = new PackageResourceReference(scope, "resource_gt_4096.txt",
			null, null, null);

		ByteArrayResponse byteResponse = new ByteArrayResponse();

		Request request = tester.getRequestCycle().getRequest();
		MockHttpServletRequest mockHttpServletRequest = (MockHttpServletRequest)request.getContainerRequest();
		mockHttpServletRequest.setHeader("range", range);
		Attributes mockAttributes = new Attributes(request, byteResponse);
		reference.getResource().respond(mockAttributes);
		return new String(byteResponse.getBytes());
	}

	/**
	 * See WICKET-5819 - Media tags
	 */
	@Test
	void testContentRangeHeaders()
	{
		// Test header fields
		ResourceReference reference = new PackageResourceReference(scope, "resource.txt",
			locales[1], styles[1], variations[1]);
		Request request = tester.getRequestCycle().getRequest();
		Response response = tester.getRequestCycle().getResponse();
		MockHttpServletResponse mockHttpServletResponse = (MockHttpServletResponse)response.getContainerResponse();
		Attributes mockAttributes = new Attributes(request, response);
		reference.getResource().respond(mockAttributes);
		assertEquals(ContentRangeType.BYTES.getTypeName(),
			mockHttpServletResponse.getHeader("Accept-Range"));
		// For normal: If a resource supports content range no content is delivered
		// if no "Range" header is given, but we have to deliver it, because
		// other resources then media should get the content. (e.g. CSS, JS, etc.) Browsers
		// detecting media requests and automatically add the "Range" header for
		// partial content and they don't make an initial request to detect if a media
		// resource supports Content-Range (by the Accept-Range header)
		assertEquals("resource_var_style_en.txt",
			new String(mockHttpServletResponse.getBinaryContent()));
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-6031
	 */
	@Test
	void noRequestCycle()
	{
		ThreadContext.setRequestCycle(null);

		PackageResourceReference reference = new PackageResourceReference(scope, "resource.txt",
				locales[1], styles[1], variations[1]);

		PackageResource resource = reference.getResource();
		assertNotNull(resource);

		assertEquals(locales[1], resource.getResourceStream().getLocale());
		assertEquals(styles[1], resource.getResourceStream().getStyle());
		assertEquals(variations[1], resource.getResourceStream().getVariation());
	}

	@Test
	public void getResourceWithNoStyle()
	{
		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/a.css");

		assertThat(tester.getLastResponseAsString(), not(containsString("color")));
	}

	@Test
	public void getStyleFromSession()
	{
		tester.getSession().setStyle("blue");
		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/a.css");

		assertThat(tester.getLastResponseAsString(), containsString("blue"));
	}

	@Test
	public void decodeStyleFromUrl()
	{
		tester.getSession().setStyle("blue");
		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/a.css?-orange");

		assertThat(tester.getLastResponseAsString(), containsString("orange"));
		assertThat(tester.getLastResponseAsString(), not(containsString("blue")));
	}

	@Test
	public void doNotFindNullResourceInTheCache()
	{
		IResourceStreamLocator resourceStreamLocator = mock(IResourceStreamLocator.class);
		when(resourceStreamLocator.locate(scope, "org/apache/wicket/core/request/resource/z.css",
			"orange", null, null, null, false)).thenReturn(null);

		tester.getApplication().getResourceSettings()
			.setResourceStreamLocator(new CachingResourceStreamLocator(resourceStreamLocator));

		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/z.css?-orange");
		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/z.css?-orange");

		verify(resourceStreamLocator, times(2)).locate(PackageResourceReferenceTest.class,
			"org/apache/wicket/core/request/resource/z.css", "orange", null, null, null, false);
	}

	@Test
	public void doNotFindResourceInTheCache()
	{
		IResourceStreamLocator resourceStreamLocator = mock(IResourceStreamLocator.class);
		when(resourceStreamLocator.locate(scope, "org/apache/wicket/core/request/resource/a.css",
			"yellow", null, null, null, false)).thenReturn(
			new UrlResourceStream(scope.getResource("a.css")));

		tester.getApplication().getResourceSettings()
			.setResourceStreamLocator(new CachingResourceStreamLocator(resourceStreamLocator));

		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/a.css?-yellow");
		tester.executeUrl(
			"wicket/resource/org.apache.wicket.core.request.resource.PackageResourceReferenceTest/a.css?-yellow");

		verify(resourceStreamLocator, times(2)).locate(PackageResourceReferenceTest.class,
			"org/apache/wicket/core/request/resource/a.css", "yellow", null, null, null, false);
	}

	@Test
	public void doNotFindMountedResourceInTheCache()
	{
		IResourceStreamLocator resourceStreamLocator = mock(IResourceStreamLocator.class);
		when(resourceStreamLocator.locate(scope, "org/apache/wicket/core/request/resource/a.css",
			"yellow", null, null, null, false)).thenReturn(
			new UrlResourceStream(scope.getResource("a.css")));

		tester.getApplication().getResourceSettings()
			.setResourceStreamLocator(new CachingResourceStreamLocator(resourceStreamLocator));
		tester.getApplication()
			.mountResource("/a.css", new PackageResourceReference(scope, "a.css"));

		tester.executeUrl("a.css?-yellow");
		tester.executeUrl("a.css?-yellow");

		verify(resourceStreamLocator, times(2)).locate(scope,
			"org/apache/wicket/core/request/resource/a.css", "yellow", null, null, null, false);
	}

	/**
	 * https://issues.apache.org/jira/browse/WICKET-7024
	 */
	@Test
	public void notDecodeStyleFromUrl()
	{
		tester.executeUrl(
			"wicket/bookmarkable/org.apache.wicket.core.request.resource.PackageResourceReferenceTest$TestPage?0-1.0-resumeButton&_=1730041277224");

		TestPage page = (TestPage)tester.getLastRenderedPage();

		assertThat(page.resource.getStyle(), not(is("1.0")));
	}

	public static class TestPage extends WebPage implements IMarkupResourceStreamProvider
	{
		CssPackageResource resource;

		@Override
		protected void onConfigure()
		{
			super.onConfigure();
			resource = (CssPackageResource)new PackageResourceReference(scope, "a.css")
				.getResource();
		}

		@Override
		public IResourceStream getMarkupResourceStream(MarkupContainer container,
			Class<?> containerClass)
		{
			return new StringResourceStream("<html><head></head><body></body></html>");
		}
	}

}
