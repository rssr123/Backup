export interface MFT {
  groupName: string;
  detailId?: string; // details have an ID, groups do not
  detailName?: string;
  value?: string;
  selected: boolean; // Keep track of checkbox state
  isGroup?: boolean; // Determine if this is a group header
}
