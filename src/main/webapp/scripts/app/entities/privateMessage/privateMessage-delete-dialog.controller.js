'use strict';

angular.module('reachoutApp')
	.controller('PrivateMessageDeleteController', function($scope, $uibModalInstance, entity, PrivateMessage) {

        $scope.privateMessage = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            PrivateMessage.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
