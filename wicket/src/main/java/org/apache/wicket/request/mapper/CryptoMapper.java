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
package org.apache.wicket.request.mapper;

import org.apache.wicket.Application;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.IProvider;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.crypt.ICryptFactory;
import org.apache.wicket.util.string.Strings;

/**
 * Request mapper that encrypts urls generated by another mapper.
 * 
 * @author igor.vaynberg
 */
public class CryptoMapper implements IRequestMapper
{
	private final IRequestMapper wrappedMapper;
	private final IProvider<ICrypt> cryptProvider;

	public CryptoMapper(IRequestMapper wrappedMapper, Application application)
	{
		this(wrappedMapper, new ApplicationCryptProvider(application));
	}

	public CryptoMapper(IRequestMapper wrappedMapper, IProvider<ICrypt> cryptProvider)
	{
		this.wrappedMapper = wrappedMapper;
		this.cryptProvider = cryptProvider;
	}

	public int getCompatibilityScore(Request request)
	{
		return 0;
	}

	public Url mapHandler(IRequestHandler requestHandler)
	{
		Url url = wrappedMapper.mapHandler(requestHandler);

		if (url == null)
		{
			return null;
		}

		return encryptUrl(url);
	}

	public IRequestHandler mapRequest(Request request)
	{
		Url url = decryptUrl(request.getUrl());

		if (url == null)
		{
			return null;
		}

		return wrappedMapper.mapRequest(request.requestWithUrl(url));
	}

	private ICrypt getCrypt()
	{
		ICryptFactory factory = Application.get().getSecuritySettings().getCryptFactory();
		return factory.newCrypt();
	}

	private Url encryptUrl(Url url)
	{
		Url encrypted = new Url();
		String encryptedUrlString = getCrypt().encryptUrlSafe(url.toString());
		encrypted.addQueryParameter("x", encryptedUrlString);
		return encrypted;
	}

	private Url decryptUrl(Url encryptedUrl)
	{
		if (encryptedUrl.getSegments().isEmpty() && encryptedUrl.getQueryParameters().isEmpty())
		{
			return encryptedUrl;
		}

		String encryptedUrlString = encryptedUrl.getQueryParameterValue("x").toString();
		if (Strings.isEmpty(encryptedUrlString))
		{
			return null;
		}

		Url url = null;
		try
		{
			String urlString = getCrypt().decryptUrlSafe(encryptedUrlString);
			if (!Strings.isEmpty(urlString))
			{
				url = Url.parse(urlString);
			}
		}
		catch (Exception e)
		{
			url = null;
		}

		return url;
	}

	private static class ApplicationCryptProvider implements IProvider<ICrypt>
	{
		private final Application application;

		public ApplicationCryptProvider(Application application)
		{
			this.application = application;
		}

		public ICrypt get()
		{
			return application.getSecuritySettings().getCryptFactory().newCrypt();
		}
	}

}
