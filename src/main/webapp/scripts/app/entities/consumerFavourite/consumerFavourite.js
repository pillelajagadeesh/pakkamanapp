'use strict';

angular.module('reachoutApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('consumerFavourite', {
                parent: 'entity',
                url: '/consumerFavourites',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.consumerFavourite.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourites.html',
                        controller: 'ConsumerFavouriteController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFavourite');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('consumerFavourite.detail', {
                parent: 'entity',
                url: '/consumerFavourite/{id}',
                data: {
                    authorities: ['ROLE_ADMIN'],
                    pageTitle: 'reachoutApp.consumerFavourite.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-detail.html',
                        controller: 'ConsumerFavouriteDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('consumerFavourite');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'ConsumerFavourite', function($stateParams, ConsumerFavourite) {
                        return ConsumerFavourite.get({id : $stateParams.id});
                    }]
                }
            })
            .state('consumerFavourite.new', {
                parent: 'consumerFavourite',
                url: '/new',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-dialog.html',
                        controller: 'ConsumerFavouriteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    consumerId: null,
                                    providerId: null,
                                    created: null,
                                    updated: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFavourite', null, { reload: true });
                    }, function() {
                        $state.go('consumerFavourite');
                    })
                }]
            })
            .state('consumerFavourite.edit', {
                parent: 'consumerFavourite',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-dialog.html',
                        controller: 'ConsumerFavouriteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ConsumerFavourite', function(ConsumerFavourite) {
                                return ConsumerFavourite.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFavourite', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('consumerFavourite.delete', {
                parent: 'consumerFavourite',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/consumerFavourite/consumerFavourite-delete-dialog.html',
                        controller: 'ConsumerFavouriteDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['ConsumerFavourite', function(ConsumerFavourite) {
                                return ConsumerFavourite.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('consumerFavourite', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
