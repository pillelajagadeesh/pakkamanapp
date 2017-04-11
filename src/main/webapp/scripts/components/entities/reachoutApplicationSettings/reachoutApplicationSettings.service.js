'use strict';

angular.module('reachoutApp')
    .factory('ApplicationSettings', function ($resource, DateUtils) {
        return $resource('api/reachoutApplicationSettingss/:id', {}, {
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
