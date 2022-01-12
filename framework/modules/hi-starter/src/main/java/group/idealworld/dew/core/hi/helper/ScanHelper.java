/*
 * Copyright 2020. the original author or authors.
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

package group.idealworld.dew.core.hi.helper;


import group.idealworld.dew.core.hi.exception.RTException;
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Java扫描操作.
 *
 * @author gudaoxuri
 */
public class ScanHelper {

    /**
     * 扫描获取指定包下的JVM类并执行指定操作.
     *
     * @param basePackage 要扫描的根包名
     */
    public static void scan(String basePackage, Consumer<Class<?>> executeFun) {
        String packageDir = basePackage.replace('.', '/');
        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageDir);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                switch (url.getProtocol()) {
                    case "file":
                        findAndAddClassesByFile(
                                basePackage, new File(URLDecoder.decode(url.getFile(), "UTF-8")), executeFun);
                        break;
                    case "jar":
                        findAndAddClassesByJar(
                                ((JarURLConnection) url.openConnection()).getJarFile(), packageDir, executeFun);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            throw new RTException(e);
        }
    }

    private static void findAndAddClassesByFile(String currentPackage, File currentFile, Consumer<Class<?>> executeFun) {
        if (currentFile.exists() && currentFile.isDirectory()) {
            File[] files = currentFile.listFiles(file -> file.isDirectory() || file.getName().endsWith(".class"));
            for (File file : files) {
                if (file.isDirectory()) {
                    findAndAddClassesByFile(currentPackage + "." + file.getName(), file, executeFun);
                } else {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    try {
                        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(currentPackage + '.' + className);
                        executeFun.accept(clazz);
                    } catch (Throwable e) {
                        // Ignore NoClassDefFoundError when class extends/implements some not import class.
                    }
                }
            }
        }
    }

    private static void findAndAddClassesByJar(JarFile jar, String currentPath, Consumer<Class<?>> executeFun) {
        Enumeration<JarEntry> entries = jar.entries();
        JarEntry jarEntry;
        String jarName;
        while (entries.hasMoreElements()) {
            jarEntry = entries.nextElement();
            jarName = jarEntry.getName();
            if (jarName.charAt(0) == '/') {
                jarName = jarName.substring(1);
            }
            if (jarName.startsWith(currentPath)) {
                int idx = jarName.lastIndexOf('/');
                if (jarName.endsWith(".class")
                        && !jarEntry.isDirectory()) {
                    String className = jarName.substring(jarName.lastIndexOf('/') + 1,
                            jarName.length() - 6);
                    try {
                        Class<?> clazz = Class.forName(jarName.substring(0, idx).replace('/', '.') + '.' + className);
                        executeFun.accept(clazz);
                    } catch (Throwable e) {
                        // Ignore NoClassDefFoundError when class extends/implements some not import class.
                    }
                }
            }
        }
    }

}
