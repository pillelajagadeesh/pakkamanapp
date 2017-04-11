(function() {
    'use strict';
    angular
        .module('reachoutApp')
        .factory('Cities', Cities);

    Cities.$inject = ['$resource'];

    function Cities ($resource) {
        var resourceUrl =  'api/cities/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
