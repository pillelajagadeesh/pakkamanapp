'use strict';

angular.module('reachoutApp')
    .factory('Provider', function ($resource, DateUtils) {
        return $resource('api/providers/:id', {}, {
        	
            'query': { method: 'GET', isArray: true},
            'getConsumerLocation': {method: 'GET', JSON: true, url: 'api/consumermaps'},
            'getProviderLocation': {method: 'GET', JSON: true, url: 'api/maps'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    data.updated = DateUtils.convertDateTimeFromServer(data.updated);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
        
    });
