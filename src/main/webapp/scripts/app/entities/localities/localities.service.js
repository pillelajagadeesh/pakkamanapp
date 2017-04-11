(function() {
    'use strict';
    angular
        .module('reachoutApp')
        .factory('Localities', Localities);

    Localities.$inject = ['$resource'];

    function Localities ($resource) {
        var resourceUrl =  'api/localities/:id';

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
