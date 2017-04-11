'use strict';

angular.module('reachoutApp').controller('RegionDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Region',
        function($scope, $stateParams, $uibModalInstance, entity, Region) {

        $scope.region = entity;
        $scope.load = function(id) {
            Region.get({id : id}, function(result) {
                $scope.region = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:regionUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.region.id != null) {
                Region.update($scope.region, onSaveSuccess, onSaveError);
            } else {
                Region.save($scope.region, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForCreated = {};

        $scope.datePickerForCreated.status = {
            opened: false
        };

        $scope.datePickerForCreatedOpen = function($event) {
            $scope.datePickerForCreated.status.opened = true;
        };
}]);
