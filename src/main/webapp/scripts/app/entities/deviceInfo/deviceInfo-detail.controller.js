'use strict';

angular.module('reachoutApp')
    .controller('DeviceInfoDetailController', function ($scope, $rootScope, $stateParams, entity, DeviceInfo) {
        $scope.deviceInfo = entity;
        $scope.load = function (id) {
            DeviceInfo.get({id: id}, function(result) {
                $scope.deviceInfo = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:deviceInfoUpdate', function(event, result) {
            $scope.deviceInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
