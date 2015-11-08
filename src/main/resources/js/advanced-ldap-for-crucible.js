AJS.$(document).ready(function() {
    AJS.$("#ldap-resync").click(function() {
        FECRU.XSRF.postUri("advancedLdapConfigurationAdminServletSync.do");
    });
    AJS.$("#ldap-edit").click(function() {
        FECRU.XSRF.postUri("advancedLdapConfigurationAdminServletEdit.do");
    });
    AJS.$("#ldap-test").click(function() {
        FECRU.XSRF.postUri("advancedLdapConfigurationTestServletTest.do");
    });
    AJS.$("#ldap-remove").click(function() {
        FECRU.XSRF.postUri("advancedLdapConfigurationAdminServletRemove.do");
    });

    AJS.$("#server-settings-toggle-arrow").click(function() {
        toggleSection("#server-settings");
    });
    AJS.$("#ldap-schema-toggle-arrow").click(function() {
        toggleSection("#ldap-schema");
    });
    AJS.$("#user-schema-toggle-arrow").click(function() {
        toggleSection("#user-schema");
    });
    AJS.$("#group-schema-toggle-arrow").click(function() {
        toggleSection("#group-schema");
    });
    AJS.$("#membership-schema-toggle-arrow").click(function() {
        toggleSection("#membership-schema");
    });
    AJS.$("#advanced-settings-toggle-arrow").click(function() {
        toggleSection("#advanced-settings");
    });

    function toggleSection(element) {
        AJS.$(element).toggle(250);
        if (AJS.$(element + "-toggle-arrow").hasClass("aui-iconfont-arrows-down"))
        {
            AJS.$(element + "-toggle-arrow").removeClass("aui-iconfont-arrows-down");
            AJS.$(element + "-toggle-arrow").addClass("aui-iconfont-arrows-right");
        } else {
            AJS.$(element + "-toggle-arrow").removeClass("aui-iconfont-arrows-right");
            AJS.$(element + "-toggle-arrow").addClass("aui-iconfont-arrows-down");
        }
    }
});
