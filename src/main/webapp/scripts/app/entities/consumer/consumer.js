'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('consumer', {
                parent: 'entity',
                url: '/consumers',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumer.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumer/consumers.html',
                        controller: 'ConsumerController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumer');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            
            .state('consumer.detail', {
                parent: 'entity',
                url: '/consumer/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'reachoutApp.consumer.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumer/consumer-detail.html',
                        controller: 'ConsumerDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumer');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Consumer', function($stateParams, Consumer) {
                        return Consumer.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumer.new', {
                parent: 'consumer',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumer/consumer-dialog.html',
                        controller: 'ConsumerDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    mobile: null,
                                    email: null,
                                    status: null,
                                    otp: null,
                                    otpCount: null,
                                    created: null,
                                    updated: null,
                                    name: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumer', null, { reload: true });
                    }, function() {
                        $state.go('consumer');
                    })
                }]
            })
             
            .state('consumer.edit', {
                parent: 'consumer',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumer/consumer-dialog.html',
                        controller: 'ConsumerDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Consumer', function(Consumer) {
                                return Consumer.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumer', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumer.delete', {
                parent: 'consumer',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumer/consumer-delete-dialog.html',
                        controller: 'ConsumerDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Consumer', function(Consumer) {
                                return Consumer.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumer', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
                
               
            });
    });
