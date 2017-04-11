'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('consumerFeedback', {
                parent: 'entity',
                url: '/consumerFeedbacks',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.consumerFeedback.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedbacks.html',
                        controller: 'ConsumerFeedbackController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFeedback');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumerFeedback.detail', {
                parent: 'entity',
                url: '/consumerFeedback/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.consumerFeedback.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-detail.html',
                        controller: 'ConsumerFeedbackDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFeedback');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ConsumerFeedback', function($stateParams, ConsumerFeedback) {
                        return ConsumerFeedback.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumerFeedback.new', {
                parent: 'consumerFeedback',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-dialog.html',
                        controller: 'ConsumerFeedbackDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    comment: null,
                                    likeOrDislike: null,
                                    notificationId: null,
                                    consumerId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFeedback', null, { reload: true });
                    }, function() {
                        $state.go('consumerFeedback');
                    })
                }]
            })
            .state('consumerFeedback.edit', {
                parent: 'consumerFeedback',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-dialog.html',
                        controller: 'ConsumerFeedbackDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ConsumerFeedback', function(ConsumerFeedback) {
                                return ConsumerFeedback.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFeedback', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumerFeedback.delete', {
                parent: 'consumerFeedback',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFeedback/consumerFeedback-delete-dialog.html',
                        controller: 'ConsumerFeedbackDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ConsumerFeedback', function(ConsumerFeedback) {
                                return ConsumerFeedback.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFeedback', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
