package com.android.mycamera.cameraapplication;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import android.test.suitebuilder.TestSuiteBuilder;
/**
 * Created by vk on 8/4/2016.
 */
public class Tests extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(Tests.class)
                .includeAllPackagesUnderHere()
                .build();
    }

}
