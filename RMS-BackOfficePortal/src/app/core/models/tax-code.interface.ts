export interface TaxCode {
    taxCd: any;
    tax_cd: string;
    tax_cd_nm_en: string;
    tax_cd_nm_bm: string;
    tax_pct: number;
    dtCreated: Date;
    dtModified: Date;
    createdBy: string;
    modifiedBy: string;
    status_en: string;
    status_bm: string;

    isNew?: boolean;
    isEditable?: boolean;

}

export interface TaxCodeTest {
    taxCd: any;
    tax_cd: string;
    tax_cd_nm_en: string;
    tax_cd_nm_bm: string;
    tax_pct: number;
    dtCreated: Date;
    dtModified: Date;
    createdBy: string;
    modifiedBy: string;
    status_en: string;
    status_bm: string;

    isNew?: boolean;
    isEditable?: boolean;
    autoSuggest: string;
    filteredSuggestions?: string[]; // Add this property
}