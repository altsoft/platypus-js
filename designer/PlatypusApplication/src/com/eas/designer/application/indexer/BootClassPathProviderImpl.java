/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import com.eas.designer.application.platform.PlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.java.classpath.support.PathResourceBase;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 * The platform JS API class path provider.
 *
 * @author vv
 */
@ServiceProvider(service = ClassPathProvider.class)
public class BootClassPathProviderImpl implements ClassPathProvider {

    ClassPath bootClassPath;

    @Override
    public ClassPath findClassPath(FileObject file, String type) {
        if (type.equals(PlatypusPathRecognizer.BOOT_CP)) {
            return getBootClassPath();
        }
        return null;
    }

    public void registerJsClassPath() {
    }

    public ClassPath getBootClassPath() {
        if (bootClassPath == null) {
            bootClassPath = ClassPathSupport.createClassPath(Collections.singletonList(new BootResourceImpl()));
        }
        return bootClassPath;
    }

    private File getBootDirectory() {
        File dir = null;
        try {
            dir = PlatypusPlatform.getPlatformApiDirectory();
        } catch (PlatformHomePathException ex) {
            //no-op
        }
        return dir;
    }

    public class BootResourceImpl extends PathResourceBase {

        @Override
        public URL[] getRoots() {
            File bootDir = getBootDirectory();
            List<URL> urls = new ArrayList<>();
            if (bootDir != null) {
                try {
                    urls.add(Utilities.toURI(bootDir).toURL());
                } catch (MalformedURLException ex) {
                    ErrorManager.getDefault().notify(ex);//Should never happen
                }
            }
            return urls.toArray(new URL[0]);
        }

        @Override
        public ClassPathImplementation getContent() {
            return null;
        }
    }
}
