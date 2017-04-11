(function() {
    'use strict';
    angular
        .module('reachoutApp')
        .factory('PakkaApplicationSettings', PakkaApplicationSettings);

    PakkaApplicationSettings.$inject = ['$resource'];

    function PakkaApplicationSettings ($resource) {
        var resourceUrl =  'api/pakka-application-settings/:id';

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
