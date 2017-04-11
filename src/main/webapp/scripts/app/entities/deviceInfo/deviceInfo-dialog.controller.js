'use strict';

angular.module('reachoutApp').controller('DeviceInfoDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'DeviceInfo',
        function($scope, $stateParams, $uibModalInstance, entity, DeviceInfo) {

        $scope.deviceInfo = entity;
        $scope.load = function(id) {
            DeviceInfo.get({id : id}, function(result) {
                $scope.deviceInfo = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:deviceInfoUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.deviceInfo.id != null) {
                DeviceInfo.update($scope.deviceInfo, onSaveSuccess, onSaveError);
            } else {
                DeviceInfo.save($scope.deviceInfo, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
