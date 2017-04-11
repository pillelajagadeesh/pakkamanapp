'use strict';

angular.module('reachoutApp')
	.controller('DeviceInfoDeleteController', function($scope, $uibModalInstance, entity, DeviceInfo) {

        $scope.deviceInfo = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            DeviceInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
