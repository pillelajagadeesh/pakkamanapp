(function() {
    'use strict';
    angular
        .module('reachoutApp')
        .factory('Category', Category);

    Category.$inject = ['$resource', 'DateUtils'];

    function Category ($resource, DateUtils) {
        var resourceUrl =  'api/categories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.created = DateUtils.convertDateTimeFromServer(data.created);
                        data.deleted = DateUtils.convertDateTimeFromServer(data.deleted);
                        data.lastUpdate = DateUtils.convertDateTimeFromServer(data.lastUpdate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
