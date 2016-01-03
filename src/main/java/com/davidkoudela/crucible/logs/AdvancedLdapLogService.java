package com.davidkoudela.crucible.logs;

import org.apache.log4j.Level;

/**
 * Description: {@link AdvancedLdapLogService} represents an interface of a service for managing logs.
 * Copyright (C) 2015 David Koudela
 *
 * @author dkoudela
 * @since 2016-01-03
 */
public interface AdvancedLdapLogService {
    public void setLogLevel(Level level);
}
