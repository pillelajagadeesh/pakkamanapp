'use strict';

angular.module('reachoutApp')
	.controller('NotificationAcknowledgementDeleteController', function($scope, $uibModalInstance, entity, NotificationAcknowledgement) {

        $scope.notificationAcknowledgement = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            NotificationAcknowledgement.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
