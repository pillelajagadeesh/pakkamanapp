'use strict';

angular.module('reachoutApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


