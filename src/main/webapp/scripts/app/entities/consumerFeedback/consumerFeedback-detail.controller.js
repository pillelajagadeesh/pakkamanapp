'use strict';

angular.module('reachoutApp')
    .controller('ConsumerFeedbackDetailController', function ($scope, $rootScope, $stateParams, entity, ConsumerFeedback) {
        $scope.consumerFeedback = entity;
        $scope.load = function (id) {
            ConsumerFeedback.get({id: id}, function(result) {
                $scope.consumerFeedback = result;
            });
        };
        var unsubscribe = $rootScope.$on('reachoutApp:consumerFeedbackUpdate', function(event, result) {
            $scope.consumerFeedback = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
