'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('consumerRegions', {
                parent: 'entity',
                url: '/consumerRegionss',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.consumerRegions.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegionss.html',
                        controller: 'ConsumerRegionsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerRegions');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumerRegions.detail', {
                parent: 'entity',
                url: '/consumerRegions/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.consumerRegions.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-detail.html',
                        controller: 'ConsumerRegionsDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerRegions');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ConsumerRegions', function($stateParams, ConsumerRegions) {
                        return ConsumerRegions.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumerRegions.new', {
                parent: 'consumerRegions',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-dialog.html',
                        controller: 'ConsumerRegionsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    consumerId: null,
                                    region: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumerRegions', null, { reload: true });
                    }, function() {
                        $state.go('consumerRegions');
                    })
                }]
            })
            .state('consumerRegions.edit', {
                parent: 'consumerRegions',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-dialog.html',
                        controller: 'ConsumerRegionsDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ConsumerRegions', function(ConsumerRegions) {
                                return ConsumerRegions.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerRegions', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumerRegions.delete', {
                parent: 'consumerRegions',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerRegions/consumerRegions-delete-dialog.html',
                        controller: 'ConsumerRegionsDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ConsumerRegions', function(ConsumerRegions) {
                                return ConsumerRegions.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerRegions', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
