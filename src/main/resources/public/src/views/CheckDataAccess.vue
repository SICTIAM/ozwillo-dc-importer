<template>
    <div class="container">
        <h2>{{ $t('data_access_check') }}</h2>
        <p>{{ dataRequest.model }}</p>
        <div class="form-check">
            <input id="agreeAllData" class="form-check-input" type="checkbox" :value="agreeAllData" @change="handleAgreeAllData">
            <label for="agreeAllData" class="form-check-label">
                {{ $t('authorize_access_to_all_checked_data', {modelName: dataRequest.model}) }}
            </label>
        </div>
        <div class="form-group row" v-if="dataRequest.fields != null">
            <label for="fields-model" class="col-sm-3 col-form-label col-form-label-sm">
                {{ $t('fields') }}
            </label>
            <ul id="fields-model" class="list-group">
                <li class="list-group-item" v-for="field in dataRequest.fields">
                    <input type="checkbox" :id="field.name" v-model="field.requested" disabled>
                    <label :for="field.name">{{ field.name }}</label>
                </li>
            </ul>
        </div>
        <button class="btn btn-outline-primary" type="button" @click="validateAccess" :disabled="!agreeAllData">
            {{ $t('validate') }}
        </button>
        <button class="btn btn-outline-primary" type="button" @click="refuseAccess">
            {{ $t('refuse') }}
        </button>
    </div>
</template>

<script>
    import axios from 'axios'
    import DataAccessRequestMixin from '@/utils/mixins/DataAccessRequestMixin'

    export default {
        name: 'CheckDataAccess',
        mixins: [DataAccessRequestMixin],
        data() {
            return {
                agreeAllData: false
            }
        },
        created() {
            this.fetchDataAccessRequest(this.$route.params.id)
        },
        methods: {
            handleAgreeAllData() {
              this.agreeAllData = !this.agreeAllData
            },
            validateAccess() {
                this.modifyStateOfRequest('valid', this.dataRequest.id)
            },
            refuseAccess() {
                this.modifyStateOfRequest('reject', this.dataRequest.id)
            }
        }
    }
</script>

<style scoped>
    .validation-buttons {
        margin-top: 50px;
        margin-right: 5px;
    }
</style>


