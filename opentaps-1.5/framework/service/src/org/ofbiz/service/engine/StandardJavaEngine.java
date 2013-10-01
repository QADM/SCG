/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/* This file has been modified by Open Source Strategies, Inc. */
package org.ofbiz.service.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;

/**
 * Standard Java Static Method Service Engine
 */
public final class StandardJavaEngine extends GenericAsyncEngine {

    public static final String module = StandardJavaEngine.class.getName();

    public StandardJavaEngine(ServiceDispatcher dispatcher) {
        super(dispatcher);
    }

    /**
     * @see org.ofbiz.service.engine.GenericEngine#runSyncIgnore(java.lang.String, org.ofbiz.service.ModelService, java.util.Map)
     */
    @Override
    public void runSyncIgnore(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        runSync(localName, modelService, context);
    }

    /**
     * @see org.ofbiz.service.engine.GenericEngine#runSync(java.lang.String, org.ofbiz.service.ModelService, java.util.Map)
     */
    @Override
    public Map<String, Object> runSync(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        Object result = serviceInvoker(localName, modelService, context);

        if (result == null || !(result instanceof Map)) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] No se puedo regresar el objeto Map");
        }
        return UtilGenerics.checkMap(result);
    }

    // Invoke the static java method service.
    private Object serviceInvoker(String localName, ModelService modelService, Map<String, Object> context) throws GenericServiceException {
        // static java service methods should be: public Map<String, Object> methodName(DispatchContext dctx, Map<String, Object> context)
        DispatchContext dctx = dispatcher.getLocalContext(localName);

        if (modelService == null) {
            Debug.logError("ERROR: Null Model Service.", module);
        }
        if (dctx == null) {
            Debug.logError("ERROR: Null DispatchContext.", module);
        }
        if (context == null) {
            Debug.logError("ERROR: Null Service Context.", module);
        }

        Object result = null;

        // check the package and method names
        if (modelService.location == null || modelService.invoke == null) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] falta de ubicacion y / o invocar a los valores que son necesarios para la ejecucion.");
        }

        // get the classloader to use
        ClassLoader cl = null;

        if (dctx == null) {
            cl = this.getClass().getClassLoader();
        } else {
            cl = dctx.getClassLoader();
        }

        try {
            Class<?> c = cl.loadClass(this.getLocation(modelService));
            Method m = c.getMethod(modelService.invoke, DispatchContext.class, Map.class);
            if (Modifier.isStatic(m.getModifiers())) {
                result = m.invoke(null, dctx, context);
            } else {
                result = m.invoke(c.newInstance(), dctx, context);
            }
        } catch (ClassNotFoundException cnfe) {
            throw new GenericServiceException("No se puede encontrar servicio [" + modelService.name + "] ubicacion de la clase", cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] metodo Java especificado (invocar atributo) no existe", nsme);
        } catch (SecurityException se) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] Acceso denegado", se);
        } catch (IllegalAccessException iae) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] Metodo no accesible", iae);
        } catch (IllegalArgumentException iarge) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] Parametro no valido", iarge);
        } catch (InvocationTargetException ite) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] se produjo una excepcion inesperada", ite.getTargetException());
        } catch (NullPointerException npe) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] se encontro con un objeto nulo inesperado", npe);
        } catch (ExceptionInInitializerError eie) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] Error de inicializacion", eie);
        } catch (Throwable th) {
            throw new GenericServiceException("Servicio [" + modelService.name + "] Error o excepcion desconocida", th);
        }

        return result;
    }
}

