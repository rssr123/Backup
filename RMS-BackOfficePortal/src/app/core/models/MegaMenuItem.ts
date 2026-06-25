export interface MegaMenuItem {
  title: string;
  permission: boolean;
  subItems: { label: string; route: string }[];
}
