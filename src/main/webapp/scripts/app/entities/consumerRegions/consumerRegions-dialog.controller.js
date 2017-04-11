'use strict';

angular.module('reachoutApp').controller('ConsumerRegionsDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ConsumerRegions',
        function($scope, $stateParams, $uibModalInstance, entity, ConsumerRegions) {

        $scope.consumerRegions = entity;
        $scope.load = function(id) {
            ConsumerRegions.get({id : id}, function(result) {
                $scope.consumerRegions = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:consumerRegionsUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.consumerRegions.id != null) {
                ConsumerRegions.update($scope.consumerRegions, onSaveSuccess, onSaveError);
            } else {
                ConsumerRegions.save($scope.consumerRegions, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
