'use strict';

angular.module('reachoutApp').controller('NotificationAcknowledgementDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'NotificationAcknowledgement',
        function($scope, $stateParams, $uibModalInstance, entity, NotificationAcknowledgement) {

        $scope.notificationAcknowledgement = entity;
        $scope.load = function(id) {
            NotificationAcknowledgement.get({id : id}, function(result) {
                $scope.notificationAcknowledgement = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('reachoutApp:notificationAcknowledgementUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.notificationAcknowledgement.id != null) {
                NotificationAcknowledgement.update($scope.notificationAcknowledgement, onSaveSuccess, onSaveError);
            } else {
                NotificationAcknowledgement.save($scope.notificationAcknowledgement, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
