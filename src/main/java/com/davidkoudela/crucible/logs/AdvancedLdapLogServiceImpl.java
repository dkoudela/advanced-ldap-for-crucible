package com.davidkoudela.crucible.logs;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Description: Implementation of {@link AdvancedLdapLogService} providing a service for managing logs.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2016-01-03
 */
@Component("advancedLdapLogService")
public class AdvancedLdapLogServiceImpl implements AdvancedLdapLogService {
    private static final String LOG_ROOT = "com.davidkoudela.crucible";

    @Override
    synchronized public void setLogLevel(Level level) {
        Logger logger = org.apache.log4j.Logger.getLogger(LOG_ROOT);
        if (level != logger.getLevel()) {
            logger.setLevel(level);
        }
    }
}
