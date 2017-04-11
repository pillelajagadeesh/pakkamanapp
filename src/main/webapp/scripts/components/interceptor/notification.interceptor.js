 'use strict';

angular.module('reachoutApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-reachoutApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-reachoutApp-params')});
                }
                return response;
            }
        };
    });
