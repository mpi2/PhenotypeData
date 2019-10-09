package org.mousephenotype.cda.loads.common;

import joptsimple.OptionParser;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.LoggerFactory;

public class CommandLineUtils {
    private static final org.slf4j.Logger logger   = LoggerFactory.getLogger(CommonUtils.class);

    public static OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        return parser;
    }
}
