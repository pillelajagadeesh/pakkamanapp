'use strict';

angular.module('reachoutApp')
    .factory('NotificationAcknowledgement', function ($resource, DateUtils) {
        return $resource('api/notificationAcknowledgements/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
