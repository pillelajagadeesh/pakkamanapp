'use strict';

angular.module('reachoutApp')
    .factory('Notification', function ($resource, DateUtils) {
        return $resource('api/notifications/:id', {}, {
            'query': { method: 'GET', isArray: true,params:{category:'@category',status:'@status',mainCategory:'@mainCategory'}},
            'getConsumerNotifications': {method: 'GET',isArray: true,params: {id:'@id'}, url: 'api/getConsumerNotifications/:id'},
            'blockNotification': {method: 'POST',isArray: true,params: {id:'@id'}, url: 'api/notifications/suspend/:id'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.validFrom =DateUtils.convertDateTimeFromServer(data.validFrom);
                    data.validTo = DateUtils.convertDateTimeFromServer(data.validTo);
                    data.expieryDate = DateUtils.convertDateTimeFromServer(data.expieryDate);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    data.updated = DateUtils.convertDateTimeFromServer(data.updated);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });

