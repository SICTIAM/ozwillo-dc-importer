<template>
    <div id="container" class="container">
        <h2>Request for access to dataset</h2>
        <form>
            <div class="form-group row">
                <label for="claimer-organization" class="col-sm-3 col-form-label col-form-label-sm">
                    Organization
                </label>
                <vue-bootstrap-typeahead
                    id="claimer-organization"
                    placeholder="Find a organization"
                    v-model="organizationSearch"
                    :data="organizations"
                    :serializer="displayingResultOfOrganizationSearch"
                    @hit="organizationSelected = $event"/>
            </div>
            <div class="form-group row">
                <label for="claimer-email" class="col-sm-3 col-form-label col-form-label-sm">
                    Email
                </label>
                <input id="claimer-email" v-model="dataAccessRequest.email"/>
            </div>
            <div class="form-group row">
                <label for="claimer-model" class="col-sm-3 col-form-label col-form-label-sm">
                    Model
                </label>
                <vue-bootstrap-typeahead
                    id="claimer-model"
                    placeholder="Find a model of dataset"
                    v-model="modelSearch"
                    :data="models"
                    @hit="modelSelected = $event"/>
            </div>
            <input type="button" @click="createDataAccessRequest" value="submit" v-bind:disabled="disabled">
        </form>
    </div>
</template>

<script>
    import axios from 'axios'
    import debounce from 'lodash/debounce'
    import VueBootstrapTypeahead from 'vue-bootstrap-typeahead'
    import VueRouter from 'vue-router'

    export default {
        name: "dataAccessRequest",
        components: {
            VueBootstrapTypeahead
        },
        data() {
            return {
                dataAccessRequest: {
                    id: null,
                    nom: '',
                    email: '',
                    organization: '',
                    model: ''
                },
                errors: [],
                organizationSelected: null,
                organizationSearch: '',
                organizations: [],
                modelSelected: '',
                modelSearch: '',
                models: []
            }
        },
        watch: {
            organizationSearch: debounce(function(name) { this.getOrganizations(name) }, 500),
            modelSearch: debounce(function(name) { this.getModels(name) }, 500)
        },
        computed: {
            disabled: function(){
                return (this.organizationSelected == false || this.dataAccessRequest.email == '' || this.modelSelected == '')
            }
        },
        beforeCreate() {
            if(this.$route.params.id != null) {
                axios.get(`/api/data_access_request/${this.$route.params.id}`)
                  .then(response => {
                      this.dataAccessRequest = response.data
                      this.$refs.typeahead.$data.inputValue = this.dataAccessRequest.model
                  })
                  .catch(e => {
                      this.errors.push(e)
                  })
            }
        },
        beforeRouteUpdate (to, from, next) {
            next()
            this.dataAccessRequest = {
                id: null,
                nom: '',
                email: '',
                organization: '',
                model: ''}
        },
        methods: {
            displayingResultOfOrganizationSearch(organization) {
                return `${organization.denominationUniteLegale}, ${organization.siret}`
            },
            createDataAccessRequest() {
                Object.assign(this.dataAccessRequest, {
                    organization: this.organizationSelected.uri,
                    model: this.modelSelected
                })
                axios.post(`/api/data_access_request/123456789/send`, this.dataAccessRequest)
                    .then(() => {
                        this.$router.push({ name: 'dashboard' })
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },
            getModels() {
                axios.get('/api/data_access_request/123456789/model', {params: {name: this.dataAccessRequest.model}})
                    .then(response => {
                        this.models = response.data
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            },
            getOrganizations(name) {
                axios.get('/api/data_access_request/organizations', {params: {name: name}})
                    .then(response => {
                        this.organizations = response.data
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            }
        }
    }
</script>

<style scoped>

</style>
