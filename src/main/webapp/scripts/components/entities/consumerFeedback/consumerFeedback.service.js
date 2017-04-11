'use strict';

angular.module('reachoutApp')
    .factory('ConsumerFeedback', function ($resource, DateUtils) {
        return $resource('api/consumerFeedbacks/:id', {}, {
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
