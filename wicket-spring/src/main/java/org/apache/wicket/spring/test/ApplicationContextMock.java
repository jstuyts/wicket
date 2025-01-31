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
package org.apache.wicket.spring.test;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Nonnull;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;

/**
 * Mock application context object. This mock context allows easy creation of unit tests by allowing
 * the user to put bean instances into the context.
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class ApplicationContextMock extends AbstractApplicationContext implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final DefaultListableBeanFactory beanFactory;
	private final long startupTime;

	public ApplicationContextMock() {
		this.beanFactory = new DefaultListableBeanFactory();
		beanFactory.setSerializationId(ApplicationContextMock.class.getName());
		startupTime = System.currentTimeMillis();
	}

	/**
	 * puts bean with the given name into the context
	 * 
	 * @param name
	 * @param bean
	 */
	public <T extends Object> void putBean(final String name, final T bean)
	{
		beanFactory.registerBeanDefinition(name, new RootBeanDefinition((Class<T>)bean.getClass(), () -> bean));
	}

	/**
	 * puts bean with into the context. bean object's class name will be used as the bean name.
	 * 
	 * @param bean
	 */
	public void putBean(final Object bean)
	{
		putBean(bean.getClass().getName(), bean);
	}

	@Nonnull
	@Override
	public Object getBean(@Nonnull final String name) throws BeansException
	{
		return beanFactory.getBean(name);
	}

	@Nonnull
	@Override
	public Object getBean(@Nonnull final String name, @Nonnull final Object... args) throws BeansException
	{
		return beanFactory.getBean(name, args);
	}

	@Nonnull
	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> T getBean(@Nonnull String name, @Nonnull Class<T> requiredType) throws BeansException
	{
		return beanFactory.getBean(name, requiredType);
	}

	@Nonnull
	@Override
	@SuppressWarnings({ "unchecked" })
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException
	{
		return beanFactory.getBeansOfType(type);
	}

	@Nonnull
	@Override
	public <T> T getBean(@Nonnull Class<T> requiredType) throws BeansException
	{
		return beanFactory.getBean(requiredType);
	}

	@Nonnull
	@Override
	public <T> T getBean(@Nonnull Class<T> requiredType, @Nonnull Object... objects) throws BeansException
	{
		return beanFactory.getBean(requiredType, objects);
	}

	@Nonnull
	@Override
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull Class<T> aClass)
	{
		return beanFactory.getBeanProvider(aClass);
	}

	@Nonnull
	@Override
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull ResolvableType resolvableType)
	{
		return beanFactory.getBeanProvider(resolvableType);
	}

	@Nonnull
	@Override
	public Map<String, Object> getBeansWithAnnotation(@Nonnull Class<? extends Annotation> annotationType)
		throws BeansException
	{
		return beanFactory.getBeansWithAnnotation(annotationType);
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(@Nonnull String beanName, @Nonnull Class<A> annotationType)
	{
		return beanFactory.findAnnotationOnBean(beanName, annotationType);
	}

	@Override
	public ApplicationContext getParent()
	{
		return null;
	}

	@Nonnull
	@Override
	public String getDisplayName()
	{
		return ApplicationContextMock.class.getSimpleName();
	}

	@Override
	public long getStartupDate()
	{
		return startupTime;
	}

	@Override
	public void publishEvent(@Nonnull final ApplicationEvent event)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void publishEvent(@Nonnull Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsBeanDefinition(@Nonnull final String beanName)
	{
		return beanFactory.containsBean(beanName);
	}

	@Override
	public int getBeanDefinitionCount()
	{
		return beanFactory.getBeanDefinitionCount();
	}

	@Nonnull
	@Override
	public String[] getBeanDefinitionNames()
	{
		return beanFactory.getBeanDefinitionNames();
	}

	@Nonnull
	@Override
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull final Class<T> aClass, final boolean b) {
		return beanFactory.getBeanProvider(aClass, b);
	}

	@Nonnull
	@Override
	public <T> ObjectProvider<T> getBeanProvider(@Nonnull final ResolvableType resolvableType, final boolean b) {
		return beanFactory.getBeanProvider(resolvableType, b);
	}

	@Nonnull
	@Override
	public String[] getBeanNamesForType(@Nonnull ResolvableType resolvableType)
	{
		return beanFactory.getBeanNamesForType(resolvableType);
	}

	@Nonnull
	@Override
	public String[] getBeanNamesForType(@Nonnull ResolvableType resolvableType, boolean includeNonSingletons, boolean allowEagerInit)
	{
		return beanFactory.getBeanNamesForType(resolvableType, includeNonSingletons, allowEagerInit);
	}

	@Nonnull
	@Override
	public String[] getBeanNamesForType(final Class type)
	{
		return beanFactory.getBeanNamesForType(type);
	}

	@Nonnull
	@Override
	public String[] getBeanNamesForType(Class type, boolean includeNonSingletons,
		boolean allowEagerInit)
	{
		return beanFactory.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Nonnull
	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
		boolean allowEagerInit) throws BeansException
	{
		return beanFactory.getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Nonnull
	@Override
	public String[] getBeanNamesForAnnotation(@Nonnull Class<? extends Annotation> aClass)
	{
		return beanFactory.getBeanNamesForAnnotation(aClass);
	}

	@Override
	public boolean containsBean(@Nonnull final String name)
	{
		return beanFactory.containsBean(name);
	}

	@Override
	public boolean isSingleton(@Nonnull final String name) throws NoSuchBeanDefinitionException
	{
		return beanFactory.isSingleton(name);
	}

	@Override
	public Class<?> getType(@Nonnull final String name) throws NoSuchBeanDefinitionException
	{
		return beanFactory.getType(name);
	}

	@Override
	public Class<?> getType(@Nonnull String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException
	{
		return beanFactory.getType(name, allowFactoryBeanInit);
	}

	@Nonnull
	@Override
	public String[] getAliases(@Nonnull final String name) throws NoSuchBeanDefinitionException
	{
		return beanFactory.getAliases(name);
	}

	@Override
	public BeanFactory getParentBeanFactory()
	{
		return beanFactory.getParentBeanFactory();
	}

	@Override
	public String getMessage(@Nonnull final String code, final Object[] args, final String defaultMessage,
		@Nonnull final Locale locale)
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public String getMessage(@Nonnull final String code, final Object[] args, @Nonnull final Locale locale)
		throws NoSuchMessageException
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public String getMessage(@Nonnull final MessageSourceResolvable resolvable, @Nonnull final Locale locale)
		throws NoSuchMessageException
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public Resource[] getResources(@Nonnull final String locationPattern) throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected void refreshBeanFactory() throws BeansException, IllegalStateException {
	}

	@Override
	protected void closeBeanFactory() {
	}

	@Nonnull
	@Override
	public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
		return beanFactory;
	}

	@Nonnull
	@Override
	public Resource getResource(@Nonnull final String location)
	{
		throw new UnsupportedOperationException();
	}

	@Nonnull
	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException
	{
		return beanFactory;
	}

	@Override
	public boolean containsLocalBean(@Nonnull final String name)
	{
		return beanFactory.containsLocalBean(name);
	}

	@Override
	public boolean isPrototype(@Nonnull final String name) throws NoSuchBeanDefinitionException
	{
		return beanFactory.isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(@Nonnull String s, @Nonnull ResolvableType resolvableType) throws NoSuchBeanDefinitionException
	{
		return beanFactory.isTypeMatch(s, resolvableType);
	}

	@Override
	public boolean isTypeMatch(@Nonnull final String name, @Nonnull final Class targetType)
		throws NoSuchBeanDefinitionException
	{
		return beanFactory.isTypeMatch(name, targetType);
	}
}
