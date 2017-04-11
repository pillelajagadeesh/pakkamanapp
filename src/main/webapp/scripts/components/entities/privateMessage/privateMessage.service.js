'use strict';

angular.module('reachoutApp')
    .factory('PrivateMessage', function ($resource, DateUtils) {
        return $resource('api/privateMessages/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
